# Optimal Poker Strategies Under Collusion
This is the data collection code I wrote for my thesis on
[**Simulating Fictitious Play in Simplified Poker Variants**](http://bobbyullman.com/files/poker/thesis-fall15.pdf).

This code was a means to an end -- noteably to compute a Nash Equilibrium in poker-like games for my thesis.
The code is not well documented and although it provides a good enough abstraction for specifying
a wide variety of poker-like games, it is not particularly performant.

## Usage

You can see a few of the computations I performed in `run.sh`, which runs `ComputeNash.java`.  
The `main()` is currently hard-coded to find a Nash Equilibrium strategy in **3-player Mercer Holdem**
-- a simple poker variant.  Running
```
java ComputeNash [pos1] [pos2] [tolerance] [output prefix]
```
computes both a non-collusion and collusion Nash Equilibrium (where `pos1` and `pos2` are the zero-indexed positions of the colluding players)
of the game via [ficticious play](https://en.wikipedia.org/wiki/Fictitious_play).

Read [the paper](http://bobbyullman.com/files/poker/thesis-fall15.pdf) for more information on
the structure of the program and analysis of the results.
