package com.jinin4.journalog

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.jinin4.journalog.calendar.CalendarFragment
import com.jinin4.journalog.databinding.ActivityMainBinding
import com.jinin4.journalog.photo.PhotoFragment
import com.jinin4.journalog.setting.SettingFragment
import com.jinin4.journalog.setting.ThemeViewModel
import com.jinin4.journalog.tag.TagFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

//이상원 - 24.01.19, 반정현 - 24.01.22 수정
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: ThemeViewModel by viewModels()
    private lateinit var binding:ActivityMainBinding
    private var isWeek = true
    override fun onCreate(savedInstanceState: Bundle?) {
        //앱 시작할 때 테마 적용
        //applyTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val navView = binding.bottomNavigation
        //테마 변경 시 테마 적용
        viewModel.theme.observe(this) { theme ->
            when (theme) {
                Preference.Theme.LIGHT_THEME_1 -> setTheme(R.style.LIGHT_THEME_1)
                Preference.Theme.LIGHT_THEME_2 -> setTheme(R.style.LIGHT_THEME_2)
                Preference.Theme.LIGHT_THEME_3 -> setTheme(R.style.LIGHT_THEME_3)
                Preference.Theme.LIGHT_THEME_4 -> setTheme(R.style.LIGHT_THEME_4)
                else->setTheme(R.style.LIGHT_THEME_1)
            }
            val typedValue = TypedValue()
            val contextTheme = this.theme // 현재 액티비티의 테마를 가져옴
            contextTheme.resolveAttribute(R.attr.pickcolor, typedValue, true)
            val pickColorResId = typedValue.resourceId
            val pickColorStateList = ContextCompat.getColorStateList(this, pickColorResId)
            // 바텀네비게이션 클릭했을 때 배경색상 변경
            navView.itemActiveIndicatorColor = pickColorStateList
        }

        checkAnyPermission(Manifest.permission.CAMERA)
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
            checkAnyPermission(Manifest.permission.READ_MEDIA_IMAGES)
        } else{
            checkAnyPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        setContentView(binding.root)


        // 바텀네비게이션 클릭했을 때 배경색상 변경
        //navView.itemActiveIndicatorColor = ContextCompat.getColorStateList(this, R.color.moon)
        navView.selectedItemId = R.id.itm_calendar_home
        // 비동기 작업을 시작합니다.
        applyTheme {
            // 초기화가 완료되었을 때 콜백 내에서 프래그먼트를 추가
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view_tag, CalendarFragment())
                    .commit()
            }
        }

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.itm_calendar_home -> {
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(androidx.fragment.R.id.fragment_container_view_tag, CalendarFragment())
                    fragmentTransaction.commit()
                    true
                }
                R.id.itm_tag -> {
                    supportFragmentManager.beginTransaction()
                        .replace(androidx.fragment.R.id.fragment_container_view_tag , TagFragment())
                        .commitAllowingStateLoss()
                    true
                }
                R.id.itm_photo -> {
                    supportFragmentManager.beginTransaction()
                        .replace(androidx.fragment.R.id.fragment_container_view_tag ,
                            PhotoFragment()
//                            TestFragment()
                        )
                        .commitAllowingStateLoss()
                    true
                }
                R.id.itm_setting -> {
                    supportFragmentManager.beginTransaction()
                        .replace(androidx.fragment.R.id.fragment_container_view_tag ,
                            SettingFragment()
                        )
                        .commitAllowingStateLoss()
                    true
                }
                else -> false
            }
        }


    }

    private fun checkAnyPermission(permissionName: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                permissionName
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 이미 권한이 허용된 경우
        } else {
            // 권한이 없는 경우 권한을 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permissionName),
                1001
            )
        }
    }

    private fun applyTheme(onThemeApplied: () -> Unit) {
        lifecycleScope.launch {
            val theme = dataStore.data.first().theme
            when (theme) {
                Preference.Theme.LIGHT_THEME_1 -> setTheme(R.style.LIGHT_THEME_1)
                Preference.Theme.LIGHT_THEME_2 -> setTheme(R.style.LIGHT_THEME_2)
                Preference.Theme.LIGHT_THEME_3 -> setTheme(R.style.LIGHT_THEME_3)
                Preference.Theme.LIGHT_THEME_4 -> setTheme(R.style.LIGHT_THEME_4)
                else->setTheme(R.style.LIGHT_THEME_1)
            }
            onThemeApplied()
        }
    }

}