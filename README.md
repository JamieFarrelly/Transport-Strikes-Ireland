# Luas Strike Information Android App

Very basic app to help people find out when the next Luas strike is in Dublin. Literally a one screen app that was quickly put together over a few hours.

The app basically makes an API call to [Firebase](https://www.firebase.com/) which returns json from the database in the format of:

```json
{"nextStrike":"13-05-2016",
 "strikeHours": "(3pm-7pm)"}
```

This is then used to work out whether there's a strike on or not, as well as to display when the next strike will be if there is on.

<a href="https://play.google.com/store/apps/details?id=com.jamiefarrelly.striketracker&hl=en">
<img alt="Get it on Google Play" src="http://steverichey.github.io/google-play-badge-svg/img/en_get.svg" />
</a>

## Media Attention

Surprisingly, it got a good bit of media attention such as:
[Joe.ie](http://www.joe.ie/start-ups/this-new-app-will-help-you-keep-track-of-the-luas-strikes/544237)
[NewsTalk](http://www.newstalk.com/reader/47.301/72635/0/)
[The Irish Sun](http://www.thesun.ie/irishsol/homepage/news/7149265/Luas-launch-app-to-warn-commuters-of-upcoming-strikes.html)
[Lovin Dublin](https://lovin.ie/cities/dublin/caught-out-by-luas-strikes-this-app-tell-alert-you-when-they-are-happening)
[SheMazing](http://www.shemazing.net/this-luas-strike-info-app-is-going-to-save-you-a-lot-of-effort-this-week/)