package lv.spkc.apturicovid.ui.home

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import lv.spkc.apturicovid.BuildConfig
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.activity.viewmodel.ExposureApiState
import lv.spkc.apturicovid.activity.viewmodel.ExposureViewModel
import lv.spkc.apturicovid.databinding.FragmentHomeBinding
import lv.spkc.apturicovid.extension.*
import lv.spkc.apturicovid.ui.BaseFragment
import lv.spkc.apturicovid.ui.statistics.StatisticsViewModel
import lv.spkc.apturicovid.ui.widget.ConfirmationDialogFragment

class HomeFragment : BaseFragment() {
    companion object {
        private const val NORMAL_SCREEN_HEIGHT_THRESHOLD_DP = 650
    }

    override val viewModel by viewModels<StatisticsViewModel> { viewModelFactory }
    private val exposureViewModel by activityViewModels<ExposureViewModel> { viewModelFactory }

    private lateinit var binding: FragmentHomeBinding

    private val exposurePanelBottomMargin by bindDimension(R.dimen.exposure_panel_bottom_margin)

    private var freshRedraw = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentHomeBinding>(inflater, R.layout.fragment_home, container, false)
            .apply { binding = this }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        freshRedraw = true

        viewModel.refreshData(requireContext())

        observeLiveData(appStatusViewModel.isExposedLiveData) {
            binding.exposedNextSteps.isEnabled = it
            if (it) {
                if (getScreenHeight() > NORMAL_SCREEN_HEIGHT_THRESHOLD_DP) {
                    binding.homeFooterCl.visible()
                    val margin = if (it) exposurePanelBottomMargin else 0
                    showExposurePanelBottomMargin(margin)
                    freshRedraw = false
                } else {
                    binding.homeFooterCl.gone()
                    showExposurePanelBottomMargin(0)
                }
            }
        }

        observeLiveData(exposureViewModel.exposureApiState) {
            binding.apply {
                val isEnabled = it == ExposureApiState.Enabled
                tracingToggle.isChecked = isEnabled

                val color = if (isEnabled) R.color.colorPrimary else R.color.colorAccent
                textHomeStatus.setTextColor(ContextCompat.getColor(requireContext(), color))
                val textResource = when(it) {
                    ExposureApiState.Enabled -> R.string.home_status_work
                    ExposureApiState.Disabled -> R.string.home_status_idle
                    ExposureApiState.Starting -> R.string.home_status_starting
                }
                textHomeStatus.text = getString(textResource)

                val image = if (isEnabled) R.drawable.ic_contact_tracing else R.drawable.ic_contact_tracing_disabled
                homeMainIv.setImageResource(image)
            }
        }

        with(binding) {
            share.setOnDebounceClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
                }
                requireActivity().startActivity(
                    Intent.createChooser(
                        shareIntent,
                        getString(R.string.app_name)
                    )
                )
            }

            tracingToggle.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    exposureViewModel.start()
                } else if (exposureViewModel.exposureApiState.value == ExposureApiState.Enabled) {
                    ConfirmationDialogFragment.newInstance(
                        R.string.confirm_turn_off_tracking_text,
                        R.string.confirm_turn_off_tracking_no,
                        R.string.confirm_turn_off_tracking_yes,
                        onAccept = { exposureViewModel.stop() },
                        onCancel = { tracingToggle.isChecked = true }
                    ).show(childFragmentManager, null)
                }
            }

            exposedNextSteps.setOnClickListener {
                Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                    .navigate(R.id.exposedNextStepsFragment)
            }

            if (BuildConfig.FLAVOR == "staging") {
                homeMainIv.setOnClickListener {
                    Navigation.findNavController(requireActivity(), R.id.main_nav_fragment)
                        .navigate(R.id.debugInfoFragment)
                }
            }

            observeLiveData(viewModel.covidStatsLiveData) {
                covidStats = it
                executePendingBindings()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        //
        // Needed to reset correct switch state after user dismisses tracing disabling dialog by
        // sending app to the background and then resuming
        //
        exposureViewModel.exposureApiState.value.let {
            binding.tracingToggle.isChecked = it == ExposureApiState.Enabled
        }
    }

    private fun showExposurePanelBottomMargin(newMargin: Int, animate: Boolean = !freshRedraw) {
        val params: ConstraintLayout.LayoutParams = binding.exposedNextSteps.layoutParams as ConstraintLayout.LayoutParams
        binding.exposedNextSteps.toggleVisibility(true)
        if (animate) {
            val animator = ValueAnimator.ofInt(params.bottomMargin, newMargin)
            animator.addUpdateListener { valueAnimator ->
                params.bottomMargin = valueAnimator.animatedValue as Int
                binding.exposedNextSteps.layoutParams = params
            }
            animator.duration = 300
            animator.start()
        } else {
            params.bottomMargin = newMargin
            binding.exposedNextSteps.layoutParams = params
        }
    }

    private fun getScreenHeight(): Float {
        val display: Display = requireActivity().windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val density = resources.displayMetrics.density
        return outMetrics.heightPixels / density
    }
}
