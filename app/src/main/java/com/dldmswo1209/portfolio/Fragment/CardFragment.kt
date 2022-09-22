package com.dldmswo1209.portfolio.Fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dldmswo1209.portfolio.MainActivity
import com.dldmswo1209.portfolio.R
import com.dldmswo1209.portfolio.WebViewActivity
import com.dldmswo1209.portfolio.adapter.CardListAdapter
import com.dldmswo1209.portfolio.databinding.FragmentCardBinding
import com.dldmswo1209.portfolio.viewModel.MainViewModel

class CardFragment : Fragment(R.layout.fragment_card) {
    private lateinit var binding : FragmentCardBinding
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_card, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCardBinding.bind(view)
        val cardAdapter = CardListAdapter { cardEntity ->
            // 어답터를 생성할 때 아이템 클릭 이벤트를 정의함
            // 아이템 클릭시 다이얼로그를 띄워 웹을 띄울 방법(내부/외부)을 선택
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("홈페이지 이동")
                .setMessage("내부 웹뷰 또는 외부 웹 브라우저를 선택해주세요.")
                .setPositiveButton("내부", DialogInterface.OnClickListener { dialog, id ->
                    val intent = Intent(requireContext(), WebViewActivity::class.java)
                    intent.putExtra("url", cardEntity.link)
                    startActivity(intent)
                })
                .setNegativeButton("외부", DialogInterface.OnClickListener { dialog, id ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(cardEntity.link))
                    startActivity(intent)
                })
            builder.show()
        }

        // 모든 카드리스트 데이터 요청
        viewModel.getAllCard()

        viewModel.cardList.observe(viewLifecycleOwner, Observer {
            // cardList 의 데이터 변화를 관찰
            // getAllCard 에 의해서 최초 실행시 데이터 변경이 관찰 됨.
            Log.d("testt", it.toString())
            cardAdapter.submitList(it)
            binding.cardRecyclerView.adapter = cardAdapter
            cardAdapter.notifyDataSetChanged()
        })

        binding.addButton.setOnClickListener {
            val bottomSheet = AddPortfolioBottomSheet()
            bottomSheet.show((activity as MainActivity).supportFragmentManager, bottomSheet.tag)
        }

    }


}