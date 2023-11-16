package com.example.myprojectapplication

import android.graphics.Color
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.SeekBar
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.myprojectapplication.databinding.FragmentCalendarBinding
import com.example.myprojectapplication.databinding.FragmentTimerBinding
import com.github.mikephil.charting.components.XAxis

/*
To do
//토마토 이미지 BG 컬러 #FFCDC0

0. 캘린더에서 작성한 할일 목록 선택하여 해당 목록에 대한 타이머
    cycle 수를 다시 캘린더로 넘겨 학습양 체크
    //뷰모델로 받아오기

1. UI 더 보기 편하게 변경

2. media player 사용 휴식 사이클에 음악 넣기
 */

class TimerFragment : Fragment() {

    //학습시간, 휴식 시간 설정
    private val STUDY_TIME = 10 * 1000L // 30분의 학습 시간 30*60*1000L
    private val REST_TIME = 5 * 1000L // 5분의 휴식 시간 5*60*100L

    private var binding: FragmentTimerBinding? = null
    private var remainMTextView: TextView? = null
    private var remainSTextView: TextView? = null
    private var currentCountTimer: CountDownTimer? = null
    private var remainCycle: TextView? = null
    private var seekBar: SeekBar? = null
    private var state: TextView? = null

    //alarmSound
    private val soundPool = SoundPool.Builder().build()
    private var beepSound: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //영역함수 apply 사용
        binding?.apply {
            remainMTextView = this.txtRemainM
            remainSTextView = this.txtRemainS
            remainCycle = this.txtCycle
            seekBar = this.seekBar1
            state = this.txtState
        }


        //seekbar 변화 감지
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val selectedCycles = progress
                    remainCycle?.text = selectedCycles.toString()
                }
            }

            //seekbar 다시 터치허면 기존 사이클 취소되도록 작성
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                currentCountTimer?.cancel()
                currentCountTimer = null
            }

            //onStopTrackingTouch에서 타이머 시작해야 중복으로 울리는 문제 사라짐.
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val cycles = seekBar?.progress ?: 0
                startStudyCycle(cycles)
            }
        })

        //사운드풀 설정
        beepSound = soundPool.load(requireContext(), R.raw.alarmsound, 1)
    }

    //학습사이클 시작 함수 +state 변수 추가하여 상단에 상태 표시
    private fun startStudyCycle(cycles: Int) {
        currentCountTimer = createCountDown(STUDY_TIME, cycles, true)
        binding?.txtState?.setTextColor(Color.RED)
        currentCountTimer?.start()
        state?.text="Study"
    }

    //휴식사이클 시작 함수
    private fun startRestCycle(cycles: Int) {
        currentCountTimer = createCountDown(REST_TIME, cycles, false)
        binding?.txtState?.setTextColor(Color.BLUE)
        currentCountTimer?.start()
        state?.text="Rest"
    }

    //타이머 생성 함수
    private fun createCountDown(time: Long, cycles: Int, isStudy: Boolean): CountDownTimer {
        return object : CountDownTimer(time, 1000L) {

            //타이머 1 tick 할 때 마다 업데이트 M, S 분리
            override fun onTick(millisUntilFinished: Long) {
                val remainSeconds = millisUntilFinished / 1000
                remainMTextView?.text = "%02d".format(remainSeconds / 60)
                remainSTextView?.text = "%02d".format(remainSeconds % 60)
            }

            //학습타이머 끝날 경우 휴식타이머, 휴식 타이머 끝나면 사이클 감소
            override fun onFinish() {
                var remainCycles = cycles

                if (time == STUDY_TIME) {
                    //beep 사운드
                    beepSound?.let{
                        soundPool.play(it, 1F, 1F, 0, 0, 1F)
                    }
                    startRestCycle(remainCycles)
                } else {
                    remainCycles--
                    remainCycle?.text = remainCycles.toString()

                    //타이머 다 끝나면 다시 TimerEntryFragment로 이동, beep 사운드: 네비게이션 전환될 때에는 실행하지 않음.
                    when(remainCycles){
                        0 -> findNavController().navigate(R.id.action_timerFragment_to_timerEntryFragment)
                        else -> {
                            beepSound?.let{
                                soundPool.play(it, 1F, 1F, 0, 0, 1F)
                            }
                            startStudyCycle(remainCycles)
                        }
                    }
                }
            }


        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        remainMTextView = null
        remainSTextView = null
        remainCycle = null
        seekBar = null

        soundPool.release()
    }
}
