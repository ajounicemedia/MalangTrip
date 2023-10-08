package com.example.malangtrip

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.malangtrip.databinding.ActivityMainHomeBinding
import com.example.malangtrip.nav.chat.chatlist.ChatList
import com.example.malangtrip.nav.community.MainCommunity
import com.example.malangtrip.nav.home.MainHome
import com.example.malangtrip.nav.myinfo.MainMyinfo
import com.example.malangtrip.nav.wishlist.MainWishlist
//메인 화면
class MainScreen : AppCompatActivity() {
    lateinit var binding: ActivityMainHomeBinding
    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //supportActionBar?.apply {
           // setDisplayShowCustomEnabled(true)
            //setDisplayShowTitleEnabled(false)  // 필요한 경우 제목 표시를 비활성화합니다.
         //   setCustomView(R.layout.action_bar)
       // }
        //알림권한 물어보기
        askNotificationPermission()
        //처음에 홈으로 세팅
        val homeFragment = MainHome()
        transaction.replace(R.id.fragmentContainer, homeFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        binding.navigationView.selectedItemId = R.id.item_home
        //밑에 5개 네비바메뉴 선택했을 때 코드
        binding.navigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_home -> { // 홈 버튼 눌렀을 때 모든 프래그먼트 제거 or 홈프래그먼트로
                    goToFragment(MainHome())
                    restoreActionBar()
                    true
                }
                R.id.item_chat -> { //채팅 메뉴로
                    goToFragment(ChatList())
                    true
                }
                R.id.item_wishlist -> {//찜 목록으로
                    goToFragment(MainWishlist())
                    true
                }
                R.id.item_myinfo -> {//내 정보로
                    goToFragment(MainMyinfo())
                    true
                }
                R.id.item_community -> {// 커뮤니티 메뉴로
                    goToFragment(MainCommunity())
                    true
                }
                else -> false
            }
        }
    }

    //액션바 원상복구하는 함수
    private fun restoreActionBar() {
        supportActionBar?.apply {
            title = "MalangTrip"
            //setDisplayHomeAsUpEnabled(false)
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // 알림권한 없음
        }
    }
    //권한 요청하기
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showPermissionRationalDialog()
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionRationalDialog() {
        AlertDialog.Builder(this)
            .setMessage("알림 권한이 없으면 알림을 받을 수 없습니다.")
            .setPositiveButton("권한 허용하기") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }.setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.cancel() }
            .show()
    }
    private fun goToFragment(fragment: Fragment)
    {
        val transaction = fragmentManager.beginTransaction()
        val goFragment = fragment
        transaction.replace(R.id.fragmentContainer, goFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}