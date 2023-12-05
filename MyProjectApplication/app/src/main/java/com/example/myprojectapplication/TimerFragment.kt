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
To DO
1. Timer UI 변경? : 요소가 더 많아지면 복잡하지 않을지 고민해보기
 */

/*
!!(널 아님 단언)
todo는 _todo를 언래핑
_todo가 무조건 Null이 아니라는 조건
프래그먼트 생성시 받은 아규먼츠에서 todo가져오기 때문에 무조건 Todo가 있어야만 하며 없으면 버그
아래 바인딩과 TimerEntryFragment의 바인딩도 마찬가지
https://developer.android.com/topic/libraries/view-binding?hl=ko

binding은 get()을 통해서 _binding이 널이 아닌 경우에만 접근할 수 있도록
 */

class TimerFragment : Fragment() {
    // 바인딩, 뷰모델 등 변수 선언
    private var _todo: TodoList? = null
    private val todo get() = _todo!!
    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TodoViewModel by activityViewModels()
    private lateinit var timerViewModel: TimerViewModel //별도로 타이머 뷰모델 추가

    //기타 변수 선언
    private val STUDY_TIME = 10 * 1000L // 30분의 학습 시간 30*60*1000L
    private val REST_TIME = 5 * 1000L // 5분의 휴식 시간 5*60*100L
    private val soundPool = SoundPool.Builder().build()
    private var beepSound: Int? = null

    // UI 관련 변수들
    private var remainMTextView: TextView? = null
    private var remainSTextView: TextView? = null
    private var currentCountTimer: CountDownTimer? = null
    private var remainCycle: TextView? = null
    private var seekBar: SeekBar? = null
    private var state: TextView? = null

    private val userId: String by lazy { viewModel.currentUserId ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _todo = arguments?.getParcelable("todo") //이전 프래그먼트에서 전달한 데이터 사용
        if (savedInstanceState != null) {
            _todo = savedInstanceState.getParcelable("todo") //화면전환 등 사라진 내용 복원
        }
        //assert(_todo != null) //개발 과정에서 _todo null이 아닌지 확인용
    }

    //화면 전환 등으로 사라지는 내용 복원
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

        //타이머 뷰모델 팩토리 패턴으로 작성
        timerViewModel = ViewModelProvider(
            this,
            TimerViewModelFactory(userId, todo)
        )[TimerViewModel::class.java]

        // UI 초기화
        binding.apply {
            remainMTextView = txtRemainM
            remainSTextView = txtRemainS
            remainCycle = txtCycle
            seekBar = seekBar1
            state = txtState
            txtWhatToDO.text = todo.thing_Todo
        }

        //Seekbar 설정
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            // Seekbar 변화 감지
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val selectedCycles = progress
                    remainCycle?.text = selectedCycles.toString()
                }
            }

            // Seekbar 다시 터치하면 기존 사이클 취소
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                currentCountTimer?.cancel()
                currentCountTimer = null
            }

            //onStop에서 사이클을 시작하여 타이머 중복으로 울리던 문제 해결
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val cycles = seekBar?.progress ?: 0
                startStudyCycle(cycles)
            }
        })

        beepSound = soundPool.load(requireContext(), R.raw.alarmsound, 1)
    }


    /*
    Timer
     */

    //학습사이클 시작 함수
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
            override fun onTick(millisUntilFinished: Long) {
                val remainSeconds = millisUntilFinished / 1000
                remainMTextView?.text = "%02d".format(remainSeconds / 60)
                remainSTextView?.text = "%02d".format(remainSeconds % 60)
            }

            override fun onFinish() {
                if (time == STUDY_TIME) {
                    finishStudyCycle(cycles)
                } else {
                    finishRestCycle(cycles)
                }
            }
        }
    }

    //study cycle이 끝난 경우 : 알람 울리고 rest cycle 시작
    private fun finishStudyCycle(cycles: Int) {
        beepSound?.let { soundPool.play(it, 1F, 1F, 0, 0, 1F) }
        startRestCycle(cycles)
    }

    /*
    rest cycle이 끝난 경우

    cycle 감소, 남은 사이클 표시
    tempCycle 증가
    achivedCycle 증가, 달성 완료 했는지 체크
    cycle 남아있다면 studyCycle 호출

    onBackPressedDispatcher.onBackPressed() : 일종의 뒤로가기
    https://developer.android.com/reference/androidx/activity/OnBackPressedDispatcher
     */
    private fun finishRestCycle(remainCycles: Int) {
        var cycles = remainCycles
        cycles--
        remainCycle?.text = cycles.toString()

        timerViewModel.addTempCycles()
        if (timerViewModel.addAchievedCycle()) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        } else {
            if (cycles <= 0) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                return
            }

            beepSound?.let { soundPool.play(it, 1F, 1F, 0, 0, 1F) }
            startStudyCycle(cycles)
        }
    }
    /*
    Timer
     */

    //onDestroy : 프래그먼트가 완전히 파괴될 때 호출. -> 타이머 취소!
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
