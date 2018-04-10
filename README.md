# Notes On Transducers

Devcards to accompany a presentation about transducers.

## Development Mode

### cljs-devtools

To enable:

1. Open Chrome's DevTools,`Ctrl-Shift-i`
2. Open "Settings", `F1`
3. Check "Enable custom formatters" under the "Console" section
4. close and re-open DevTools

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### Devcards

```
lein clean
lein figwheel devcards
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449/cards.html](http://localhost:3449/cards.html).

---

To build a minified version:

```
lein clean
lein cljsbuild once hostedcards
```

Then open *resources/public/cards.html*

## Production Build

```
lein clean
lein cljsbuild once min
```

## License

Copyright © 2018 Thomas Getgood

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
