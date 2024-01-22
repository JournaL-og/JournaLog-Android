package com.jinin4.journalog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.jinin4.journalog.calendar.CalendarFragment
import com.jinin4.journalog.databinding.ActivityMainBinding
import com.jinin4.journalog.photo.PhotoFragment
import com.jinin4.journalog.setting.SettingFragment
import com.jinin4.journalog.tag.TagFragment

//이상원 - 24.01.19
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isWeek = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        R.id.itm_calendar_home
        setContentView(binding.root)

        val navView = binding.bottomNavigation
        // 바텀네비게이션 클릭했을 때 배경색상 변경
        navView.itemActiveIndicatorColor = ContextCompat.getColorStateList(this, R.color.note)
        navView.selectedItemId = R.id.itm_calendar_home
        supportFragmentManager.beginTransaction()
            .add(androidx.fragment.R.id.fragment_container_view_tag , CalendarFragment()).commit()

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





}