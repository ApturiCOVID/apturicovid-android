package lv.spkc.apturicovid.ui.statistics

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import lv.spkc.apturicovid.R
import lv.spkc.apturicovid.databinding.FragmentStatisticsBinding
import lv.spkc.apturicovid.extension.observeLiveData
import lv.spkc.apturicovid.extension.setOnDebounceClickListener
import lv.spkc.apturicovid.ui.BaseFragment

class StatisticsFragment : BaseFragment() {
    override val viewModel by viewModels<StatisticsViewModel> { viewModelFactory }
    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<FragmentStatisticsBinding>(inflater, R.layout.fragment_statistics, container, false).apply { binding = this }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.refreshData(requireContext())

        with(binding) {
            detailedStatistics.setOnDebounceClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.detailed_statistics_url)))
                startActivity(browserIntent)
            }

            observeLiveData(viewModel.covidStatsLiveData) {
                covidStats = it
                executePendingBindings()
            }
        }
    }
}
