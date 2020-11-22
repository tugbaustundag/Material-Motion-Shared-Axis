package com.smality.materialmotionsharedaxis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.lifecycle.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.transition.MaterialSharedAxis

private const val COUNT = 3
class MainActivity : AppCompatActivity() {

    lateinit var tabLayout: TabLayout
    lateinit var backButton: MaterialButton
    lateinit var nextButton: MaterialButton
    private var selected = MutableLiveData<Int>().apply {
        value = 0
    }
    //slide'daki başlık yazılarını arraylist atadık
    private val titles by lazy {
        arrayListOf(
                getString(R.string.upload_your_photos),
                getString(R.string.share),
                getString(R.string.invite_friends)
        )
    }
    //slide'daki açıklama yazılarını arraylist atadık
    private val bodies by lazy {
        arrayListOf(
                getString(R.string.upload_artistic_photos),
                getString(R.string.share_work_social_networks),
                getString(R.string.enjoy_rewards)
        )
    }
    //slide resimlerini arraylist atadık
    private val images = arrayListOf(
            R.drawable.undraw_drag,
            R.drawable.undraw_social_sharing,
            R.drawable.undraw_winners
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.walkthrough_activity)
        //arayüz elamanlarının tanımlanması
        tabLayout = findViewById(R.id.tab_layout)
        backButton=findViewById(R.id.back_button)
        nextButton=findViewById(R.id.next_button)
        //slide'daki gösterilecek öğelerin değerlerini WalkthroughFragment sınıfına gönderiyoruz
        selected.value?.let {
            val fragment = WalkthroughFragment.newInstance(
                    titles[it],
                    bodies[it],
                    images[it]
            )
            //WalkthroughFragment sınıfı FragmentContainerView yükleniyor
            supportFragmentManager.commit {
                add(R.id.fragment_container, fragment, FRAGMENT_TAG)
            }
        }

        setDotsTabLayout()
        setClickListeners()
        setSelectedObserver()
    }
    //slide gösterimleri için COUNT değeri kadar tab eklenmesi
    private fun setDotsTabLayout() {
        repeat(COUNT) {
            tabLayout.addTab(tabLayout.newTab())
        }
        tabLayout.touchables.forEach { it.isEnabled = false }

    }
    //slide'lar arasında önceki ve sonraki slide'lara geçiş yapmanız için
    //seçim değerinde değişiklik sağlama
    private fun setClickListeners() {
        backButton.setOnClickListener {
            selected.value?.let {
                selected.value = it - 1
            }
            selectFragment(forward = false)
        }
        nextButton.setOnClickListener {
            selected.value?.let {
                selected.value = it + 1
            }
            selectFragment(forward = true)
        }
    }

    private fun setSelectedObserver() {
        selected.observe(
                this,
                Observer {
                    nextButton.isEnabled = it < COUNT - 1
                    backButton.isEnabled = it > 0
                    tabLayout.apply {
                        selectTab(getTabAt(it))
                    }
                }
        )
    }
    //SharedAxis özelliğinin hangi eksende ve slide geçiş sürelerinin ne kadar sürede olacağını hazırlayan metod
    private fun buildTransition(forward: Boolean) =
        MaterialSharedAxis(MaterialSharedAxis.X, forward).apply {
            duration = 500
    }
    private fun selectFragment(forward: Boolean) {
        selected.value?.let {
            val previousFragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
            //buildTransition metodunun açıklaması üstte bulunmaktadır
            previousFragment?.exitTransition = buildTransition(forward)
            val fragment = WalkthroughFragment.newInstance(
                    titles[it],
                    bodies[it],
                    images[it]
            )
            fragment.enterTransition = buildTransition(forward)
            supportFragmentManager.commit {
                replace(R.id.fragment_container, fragment, FRAGMENT_TAG)
            }
        }
    }

    companion object {
        private const val FRAGMENT_TAG = "WALKTHROUGH_FRAGMENT"
    }
}