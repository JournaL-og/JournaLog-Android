package com.jinin4.journalog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
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
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar
    private var isWeek = true
    override fun onCreate(savedInstanceState: Bundle?) {
        //앱 시작할 때 테마 적용
        applyTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val navView = binding.bottomNavigation
        //테마 변경 시 테마 적용
        viewModel.theme.observe(this) { theme ->
            when (theme) {
                Preference.Theme.LIGHT_THEME_1 -> setTheme(R.style.LIGHT_THEME_1)
                Preference.Theme.LIGHT_THEME_2 -> setTheme(R.style.LIGHT_THEME_2)
                else->setTheme(R.style.LIGHT_THEME_1)
            }
            val typedValue = TypedValue()
            val contextTheme = this.theme // 현재 액티비티의 테마를 가져옴
            contextTheme.resolveAttribute(R.attr.pickcolor, typedValue, true)
            val pickColorResId = typedValue.resourceId
            val pickColorStateList = ContextCompat.getColorStateList(this, pickColorResId)
            navView.itemActiveIndicatorColor = pickColorStateList
        }

//        R.id.itm_calendar_home
        setContentView(binding.root)


        // 바텀네비게이션 클릭했을 때 배경색상 변경
        //navView.itemActiveIndicatorColor = ContextCompat.getColorStateList(this, R.color.moon)
        navView.selectedItemId = R.id.itm_calendar_home
        supportFragmentManager.beginTransaction()
            .add(androidx.fragment.R.id.fragment_container_view_tag , CalendarFragment()).commit()

        // 툴바 설정
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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

    private fun applyTheme() {
        lifecycleScope.launch {
            val theme = dataStore.data.first().theme
            Log.d("Theme","${theme}")
            when (theme) {
                Preference.Theme.LIGHT_THEME_1 -> setTheme(R.style.LIGHT_THEME_1)
                Preference.Theme.LIGHT_THEME_2 -> setTheme(R.style.LIGHT_THEME_2)
                else->setTheme(R.style.LIGHT_THEME_1)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                supportFragmentManager.popBackStack()  // 프래그먼트 스택에서 마지막 프래그먼트를 제거
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}