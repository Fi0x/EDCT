# Elite: Dangerous Carrier Trader
**EDCT is a tool for large quantity trades in Elite: Dangerous.
It is very useful for fleet carrier owners to find profitable trades**

## Data
**EDCT uses the trade information from
the [inara](https://inara.cz/galaxy-commodities/) website
and the [EDDN](https://github.com/EDCD/EDDN)**

_Since inara does not provide any API endpoints for trade-data,
EDCT gathers the required information directly from the HTML files
that are provided to every website user. Therefor the trade information
is always as accurate as the information on inara itself._

To reduce traffic on [inara](https://inara.cz/galaxy-commodities/),
the HTTP requests are delayed,
which results in a few minutes waiting time at the first launch of the program.
After the first launch the program will provide results much faster,
since downloaded trade data is stored in a local database.

The program also uses the [EDDN](https://github.com/EDCD/EDDN)
to keep the local database updated.

## Features
- GUI
- Search for the best commodity prices of all commodities
- Sorted result display
- Display profit/t
- Display station supply / demand
- Filters
  - Stations that have a minimum supply / demand (useful for fleet carriers)
  - Stations with large landing pads
  - Orbital stations
  - Fleet carriers
  - Odyssey stations