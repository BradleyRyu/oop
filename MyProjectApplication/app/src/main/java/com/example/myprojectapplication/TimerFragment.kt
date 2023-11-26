package com.example.myprojectapplication

import android.graphics.Color
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.myprojectapplication.databinding.FragmentTimerBinding
import com.example.myprojectapplication.viewmodel.TodoViewModel

/*
To do
//토마토 이미지 BG 컬러 #FFCDC0

UI 더 보기 편하게 변경

타이머 엔트리에서 공부한 시간 띄우기

투두리스트 수정되면 타이머엔트리에서 목표 학습량 새로 만들기

목표 학습량 달성하면 해당 항목 리사이클러뷰에 띄우지 않도록
*/
class TimerFragment : Fragment() {
    private var _todo: TodoList? = null
    private val todo get() = _todo!!
    /*
    _todo, todo 가 있어 todo는 _todo를 강제로 언래핑
    _todo가 무조건 Null이 아니라는 조건!!

    프래그먼트 생성시 받은 아규먼츠에서 todo가져오기 때문에 무조건 Todo가 있어야만 하며 없으면 버그

    아래 바인딩과 엔트리의 바인딩도 마찬가지

    https://developer.android.com/topic/libraries/view-binding?hl=ko
     */

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TodoViewModel by activityViewModels()
    private lateinit var timerViewModel: TimerViewModel

    //학습시간, 휴식 시간 설정
    private val STUDY_TIME = 10 * 1000L // 30분의 학습 시간 30*60*1000L
    private val REST_TIME = 5 * 1000L // 5분의 휴식 시간 5*60*100L

    private var remainMTextView: TextView? = null
    private var remainSTextView: TextView? = null
    private var currentCountTimer: CountDownTimer? = null
    private var remainCycle: TextView? = null
    private var seekBar: SeekBar? = null
    private var state: TextView? = null

    //alarmSound
    private val soundPool = SoundPool.Builder().build()
    private var beepSound: Int? = null

    val userId: String by lazy { viewModel.currentUserId ?: "" }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _todo = arguments?.getParcelable("todo")
        if (savedInstanceState != null) {
            _todo = savedInstanceState.getParcelable("todo")
        }

        assert(_todo != null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable("todo", todo)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timerViewModel = ViewModelProvider(
            this,
            TimerViewModelFactory(userId, todo)
        )[TimerViewModel::class.java]

        //영역함수 apply 사용
        with(binding) {
            remainMTextView = this.txtRemainM
            remainSTextView = this.txtRemainS
            remainCycle = this.txtCycle
            seekBar = this.seekBar1
            state = this.txtState

            txtWhatToDO.text = todo.thing_Todo
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

//뷰모델의 커렌트 아이디로 설정하고, 일주일 치 배열 만들어서 넣고 그래프에 불러오면 될 듯.
    /*
    fun updateTime() {
        var id = "asdf" //id 뷰모델의 currentid로 설정
        val newTime = 1000 // 임시값
        viewModel.updateTime(id, newTime)
    }
     */

    //학습사이클 시작 함수 +state 변수 추가하여 상단에 상태 표시
    private fun startStudyCycle(cycles: Int) {
        currentCountTimer = createCountDown(STUDY_TIME, cycles, true)
        _binding?.txtState?.setTextColor(Color.RED)
        currentCountTimer?.start()
        state?.text = "Study"
    }

    //휴식사이클 시작 함수
    private fun startRestCycle(cycles: Int) {
        currentCountTimer = createCountDown(REST_TIME, cycles, false)
        _binding?.txtState?.setTextColor(Color.BLUE)
        currentCountTimer?.start()
        state?.text = "Rest"
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
                    beepSound?.let {
                        soundPool.play(it, 1F, 1F, 0, 0, 1F)
                    }
                    startRestCycle(remainCycles)

                } else {
                    remainCycles--
                    remainCycle?.text = remainCycles.toString()

                    timerViewModel.addTempCycles()
                    if (timerViewModel.addAchievedCycle()) {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    } else {
                        if (remainCycles <= 0) {
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                            return
                        }

                        beepSound?.let {
                            soundPool.play(it, 1F, 1F, 0, 0, 1F)
                        }

                        startStudyCycle(remainCycles)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        currentCountTimer?.cancel()
        currentCountTimer = null

        super.onDestroy()
    }

    override fun onDestroyView() {
        _binding = null
        soundPool.release()

        super.onDestroyView()
    }
}