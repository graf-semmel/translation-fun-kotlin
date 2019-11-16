package android.tristan.heinig.translationfunkotlin.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.tristan.heinig.translationfunkotlin.MainActivity
import android.tristan.heinig.translationfunkotlin.R
import android.view.View
import android.view.ViewGroup
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ShowMostViewedActivityTest {

  @get:Rule
  var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

  private fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {

    return object : TypeSafeMatcher<View>() {
      override fun describeTo(description: Description) {
        description.appendText("Child at position $position in parent ")
        parentMatcher.describeTo(description)
      }

      public override fun matchesSafely(view: View): Boolean {
        val parent = view.parent
        return parent is ViewGroup && parentMatcher.matches(parent) && view == parent.getChildAt(position)
      }
    }
  }

  @Test
  fun switchActivitiesTest() {
    clickOnViewsButton()
    // Added a sleep statement to match the app's execution delay.
    // The recommended way to handle such scenarios is to use Espresso idling resources:
    // https://developer.android.com/training/testing/espresso/idling-resource
    waitAMoment()
    isRecyclerViewDisplayed()
    backToMainActivity()
    waitAMoment()
    isButtonDisplayed()
  }

  private fun clickOnViewsButton() {
    val appCompatButton2 = onView(allOf(ViewMatchers.withId(R.id.btn_most_viewed), withText("Show All"),
      childAtPosition(childAtPosition(withClassName(`is`("android.support.v7.widget.CardView")), 0), 2)))
    appCompatButton2.perform(scrollTo(), click())
  }

  private fun isButtonDisplayed() {
    val button = onView(allOf(withId(R.id.btn_recent),
      childAtPosition(childAtPosition(IsInstanceOf.instanceOf(android.widget.FrameLayout::class.java), 0), 2), isDisplayed()))
    button.check(matches(isDisplayed()))
  }

  private fun backToMainActivity() {
    val appCompatImageButton = onView(allOf(withContentDescription("Nach oben"),
      childAtPosition(allOf(withId(R.id.action_bar), childAtPosition(withId(R.id.action_bar_container), 0)), 1), isDisplayed()))
    appCompatImageButton.perform(click())
  }

  private fun isRecyclerViewDisplayed() {
    val recyclerView = onView(
      allOf(withId(R.id.recycler_view), childAtPosition(childAtPosition(withId(android.R.id.content), 0), 0), isDisplayed()))
    recyclerView.check(matches(isDisplayed()))
  }

  private fun waitAMoment() {
    try {
      Thread.sleep(1000)
    } catch (e: InterruptedException) {
      e.printStackTrace()
    }

  }
}
