# ⚠️ WARNING - Don't use the tool until 3.0.0.0 is released ⚠️
**The tool uses outdated data-collection-mechanics that currently won't work!**
There are some issues during its initialization phase, which can cause the **inara-website to get blocked for a while.**

# Elite: Dangerous Carrier Trader
**EDCT is a tool for large quantity trades in Elite: Dangerous.
It is very useful for fleet carrier owners to find profitable trades.**

## Features
- GUI
- Search for the best commodity prices of all commodities
- Sorted result display
- Display profit/t
- Display distance between systems
- Display station supply / demand
- Display general station information
- Display carrier information
  - How high buy / sell prices need to be
  - How high the profit for traders is
- Filters
  - Stations that have a minimum supply / demand (useful for fleet carriers)
  - Stations with large landing pads
  - Orbital stations
  - Fleet carriers
  - Odyssey stations

## Data
**EDCT uses trade information from
the [inara](https://inara.cz/galaxy-commodities/) and [EDDB](https://eddb.io/) websites
and the [EDDN](https://github.com/EDCD/EDDN).
It also uses the [EDSM](https://www.edsm.net/) API to get system coordinates.**

_Since inara does not provide any API endpoints for trade-data,
EDCT gathers the required information directly from the HTML files
that are provided to every website user. Therefor the trade information
is always as accurate as the information on [inara](https://inara.cz/galaxy-commodities/) itself._

To reduce traffic on [inara](https://inara.cz/galaxy-commodities/),
the HTTP requests are delayed,
which results in a few minutes of waiting time at the first launch of the program.
This is only required on the first launch. Every other startup will provide results much faster,
since downloaded trade data is stored in a local database.

The program also uses the [EDDN](https://github.com/EDCD/EDDN)
to keep the local database updated.

It also gathers information from [EDDB](https://eddb.io/)
to get the galactic average of each commodity.

To provide information about the distance between two systems,
the tool gathers coordinates from each system via the [EDSM](https://www.edsm.net/) API.

### For more information about how this tool operates, visit the [wiki](https://github.com/Fi0x/EDCT/wiki)

**If you find any bugs or would like a feature to be implemented,
please create a new issue [here](https://github.com/Fi0x/EDCT/issues).**
