# translation fun kotlin
This project is an example application to translate simple English phrases to German and uses common Android features and Kotlin.
* MVVW/Clean architecture
  * ViewModel
  * Repository
  * Data sources
* ROOM database
* LiveData
* RxJava
* Navigation component
* BottomBar
* Google Translation Api
* Testing
  * JUnit
  * ROOM
  * UI
* Design
  * Card layout
  * Custom AutoCompleteTextView
  * Custom RecyclerView SwipeAction to delete cards
  
Checkout the related projects
* [translation-fun realized in Java](https://github.com/graf-semmel/translation-fun)
  
To use the Google Translation Api you have to optain an ApiKey from Google Cloud Console. Using the Translation Api **is not free**, but the price for a single translation is very low. Anyway be aware of that!! You find more information here -> https://cloud.google.com/translate/pricing
* Register for Google Cloud Console
* Search for the Translation Api
* Activate the Api. You have to create or select an purchase account, where you setup your payment method.
* Create an ApiKey for the Translation Api. You can restrict your key to the Translation Api only.
* Create the following resource xml file with any name inside the app project under **app/res/values** and enter your api key.

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <string name="api_key">...ENTER YOUR API KEY HERE...</string>
</resources>
```
