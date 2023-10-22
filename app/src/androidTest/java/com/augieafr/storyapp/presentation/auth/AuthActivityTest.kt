package com.augieafr.storyapp.presentation.auth

import androidx.annotation.IdRes
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.augieafr.storyapp.R
import com.augieafr.storyapp.data.utils.EspressoIdlingResource
import com.augieafr.storyapp.presentation.home.HomeActivity
import com.google.android.material.textfield.TextInputEditText
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AuthActivityTest {
    @get:Rule
    val activity = ActivityScenarioRule(AuthActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginScenario() {
        onView(getEditText(R.id.tilEmail)).perform(typeText("uvu@gmail.com"))
        onView(getEditText(R.id.tilPassword)).perform(typeText("11111111"))
        onView(withId(R.id.btnContinue)).perform(click())
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))
        intended(hasComponent(HomeActivity::class.java.name))
    }

    @Test
    fun logoutScenario() {
        onView(withId(R.id.imgLogout)).perform(click())
        intended(hasComponent(AuthActivity::class.java.name))
    }

    private fun getEditText(@IdRes id: Int) = allOf(
        isDescendantOfA(withId(id)),
        isAssignableFrom(TextInputEditText::class.java)
    )

}