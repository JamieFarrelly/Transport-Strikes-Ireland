# Luas Strike Information Android App

Very basic app to help people find out when the next Luas strike is in Dublin. Literally a one screen app that was quickly put together over a few hours.

The app basically makes an API call to [Firebase](https://www.firebase.com/) which returns json from the database in the format of:

```json
{"nextStrike":"13-05-2016"}
```

This is then used to work out whether there's a strike on or not, as well as to display when the next strike will be if there is on.

<a href="https://play.google.com/store/apps/details?id=com.jamiefarrelly.striketracker&hl=en">
<img alt="Get it on Google Play" src="http://steverichey.github.io/google-play-badge-svg/img/en_get.svg" />
</a>
