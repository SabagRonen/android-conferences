package io.sabag.androidConferences

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.test.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector_Factory
import javax.inject.Provider

inline fun <reified F : Fragment> createFakeFragmentInjector(
        crossinline block : F.() -> Unit
) : ActivityTestRule<MainActivity> {
    return object : ActivityTestRule<MainActivity>(MainActivity::class.java) {
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            val myApp = InstrumentationRegistry.getTargetContext().applicationContext as AndroidConferencesApp
            val originalDispatchingActivityInjector = myApp.activityInjector
            var originalFragmentInjector: AndroidInjector<Fragment>? = null
            val fragmentInjector = AndroidInjector<Fragment> { fragment ->
                originalFragmentInjector?.inject(fragment)
                if (fragment is F) {
                    fragment.block()
                }
            }
            val fragmentFactory = AndroidInjector.Factory<Fragment> { fragmentInjector }
            val fragmentMap = mapOf(Pair<Class <out Fragment>, Provider<AndroidInjector.Factory<out Fragment>>>(F::class.java, Provider { fragmentFactory }))
            val activityInjector = AndroidInjector<Activity> { activity ->
                originalDispatchingActivityInjector.inject(activity)
                if (activity is MainActivity) {
                    originalFragmentInjector = activity.fragmentInjector
                    activity.fragmentInjector = DispatchingAndroidInjector_Factory.newDispatchingAndroidInjector(fragmentMap)
                }
            }
            val activityFactory = AndroidInjector.Factory<Activity> { activityInjector }
            val activityMap = mapOf(Pair<Class <out Activity>, Provider<AndroidInjector.Factory<out Activity>>>(MainActivity::class.java, Provider { activityFactory }))
            myApp.activityInjector = DispatchingAndroidInjector_Factory.newDispatchingAndroidInjector(activityMap)
        }
    }
}