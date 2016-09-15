# Random Utils

[![build](https://travis-ci.org/cbismuth/random-utils.svg?branch=master)](https://travis-ci.org/cbismuth/random-utils)
[![coverage](https://coveralls.io/repos/github/cbismuth/random-utils/badge.svg?branch=master)](https://coveralls.io/github/cbismuth/random-utils?branch=master)
[![issues](https://img.shields.io/github/issues/cbismuth/random-utils.svg)](https://github.com/cbismuth/random-utils/issues)
[![licence](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/cbismuth/random-utils/master/LICENSE.md)


This repository contains an API to quickly extract random values from a source array.

This API aims to be **scalable** (i.e. does not depend on source array size), **fair** and returns a random array **without duplicate**.

## Metrics

### Expectations

  * a source array of 1 000 000 elements
  * a random sub-array of 100 000 elements
  
### Elapsed time

  * **noob** version (filter input array with split and join) &asymp; **4 minutes**
  * **smart** version (in-place push back picked up elements) &asymp; **25 seconds**
  * **smarter** version (in-place push back picked up elements and hash-based duplicates detection) &asymp; **40 milliseconds**

