# Fuga dev assignment

Oh no, it's the SPACE INVADERS! But this time, you only see them on a very noisy radar image. Your mission is the write an program that detects space invaders in this image.

The program must take a radar image as input, like the example below, and detect any of the known list of space invaders.

### Requirements:
- Advance image detection techniques are not required, brute force match is fine
- Although a proper algorithm design is a must, we expect you to focus on code structure, proper design principles, tests, readability etc.
- Production-like code is expected

### Tips:
- The noise in the radar image can be either false positives or false negatives
- Think of edge cases ... pun intended ;)

### Important topics that will be discussed during the interview: 
- Architecture
- Modulability
- Testability
- OOP 




### Known space invaders:
~~~~
--o-----o--
---o---o---
--ooooooo--
-oo-ooo-oo-
ooooooooooo
o-ooooooo-o
o-o-----o-o
---oo-oo---
~~~~

~~~~
---oo---
--oooo--
-oooooo-
oo-oo-oo
oooooooo
--o--o--
-o-oo-o-
o-o--o-o
~~~~

### Example radar image:
~~~~
----o--oo----o--ooo--ooo---------o---oo-o----oo---o--o---------o----o------o----------------o--o--o-
--o-o-----oooooooo-oooooo-------o----o------ooo-o---o--o----o------o--o---ooo-----o--oo-o------o----
--o--------oo-ooo-oo-oo-oo------------------ooooo-----o-----o------o---o--o--o-o-o------o----o-o-o--
-------o--oooooo--o-oo-o--o-o-----oo--o-o-oo--o-oo-oo-o--------o-----o------o-ooooo---o--o--o-------
------o---o-ooo-ooo----o------o-------o---oo-ooooo-o------o----o--------o-oo--ooo-oo-------------o-o
-o--o-----o-o---o-ooooo-o-------o----o---------o-----o-oo-----------oo----ooooooo-ooo-oo------------
o-------------ooooo-o--o--o--o-------o--o-oo-oo-o-o-o----o-------------o--oooo--ooo-o----o-----o--o-
--o-------------------------oo---------oo-o-o--ooo----o-----o--o--o----o--o-o-----o-o------o-o------
-------------------o-----------------o--o---------------o--------o--oo-o-----oo-oo---o--o---o-----oo
----------o----------o------------------o--o----o--o-o------------oo------o--o-o---o-----o----------
------o----o-o---o-----o-o---------oo-o--------o---------------------------------o-o-o--o-----------
---------------o-------------o-------o-------------------o-----o---------o-o-------------o-------oo-
-o--o-------------o-o--------o--o--oo-------------o----ooo----o-------------o----------oo----o---o-o
-o--o-------------o----oo------o--o-------o--o-----------o----o-----o--o----o--oo-----------o-------
-o-----oo-------o------o---------------o--o----------o-----o-------o-----------o---o-o--oooooo-----o
-o--------o-----o-----o---------oo----oo---o-----------o---o--oooo-oo--o-------o------oo--oo--o-----
------------o-------------------o----oooo-------------oo-oo-----ooo-oo-----o-------o-oo-oooooooo---o
-----------------------------------oooooooo---o-----o-------o--oooooo-o------------o-o-ooooooo-o----
------------o------o-------o-------oo-oo--o--o---------o--o-o-o-ooooo-o--------------oo-o----o-oo-o-
---o-o----------o--------oo----o----oooooooo-------o----o-o-o-o-----o-o-----o----------ooo-oo--o---o
-o-o---------o-o---------------o--o--o--ooo---ooo-------o------oo-oo------------o--------o--o-o--o--
-------oo---------------------------o-oo----------o------o-o-------o-----o----o-----o-oo-o-----o---o
---o--------o-----o-------o-oo-----oo--oo-o----oo----------o--o---oo------oo----o-----o-------o-----
---o--ooo-o---------o-o----o------------o---------o----o--o-------o-------------o----------------oo-
---o------o----------------o----o------o------o---oo-----------o-------------o----------oo---------o
--oo---------------o--o------o---o-----o--o-------------o------o-------o-----o-----o----o------o--o-
-o-------o----------o-o-o-------o-----o--o-o-----------o-oo-----------o------o---------o-----o-o----
----------o----o-------o----o--o------o------------o---o---------------oo----o-----ooo--------------
----o--------oo----o-o----o--o------ooo----o-oooo---o--o-oo--------o-oo-----o-o---o-o--o-----oo-----
------o--------o-ooooo----o---o--o-----o---------------o-o-------o-----o----------------------------
o-------oo----o--oooooo-o---o--o------oooo----------o-oo-------o---o----------o------oo-------------
-o---o----------o--oo-oo-o---o-----o-o-----------------------oo--o------o------o--------------------
-----oo-o-o-o---ooooooooo----o----o--------o--o---oo---o------------o----------o-o---o------o-o--oo-
------o------o---ooo-o---------------------------o--o---o---o----o--o-------o-----o------o----o----o
-------o----------ooo-o-----o----o---o--o-oo--o--o-o--o------o--o-oo---ooo------------------------o-
-o-------o------o-o--ooo--o---o---oo-----o----o-------------o----o-ooo-o------o--o-o------o-o-------
---oo--o---o-o---------o---o--------------o--o-----o-------o-----o--o---o-oo--------o----o----o-----
o------o----oo-o-----------oo--o---o--------o-o------o-------o-o------o-oo---------o-----oo---------
----o--o---o-o-----------o---o------------o-------o----o--o--o--o-o---------------o-----------------
-------oo--o-o-----o-----o----o-o--o----------------------o-------o------o----oo----ooo---------o---
o-----oo-------------------o--o-----o-----------o------o-------o----o-----------o----------------o--
--o---o-------o------------o--------------------o----o--o-------------oo---o---------oo--------o----
--o--------o---------o------------o------o-------o------------o-------o---o---------ooooo-----------
------o--------------o-o-o---------o---o-------o--o-----o-------o-o----------o-----oo-ooo----------o
--o---------------o----o--oo-------------o---------o-------------------oo---------oo-o-ooo----------
-o-----------o------ooo----o----------------ooo-----o--------o--o---o-----------o-o-oooooo--------oo
-o---o-------o---o-oooo-----o-------------------o----oo-----------------o--o--------o--o------o--o--
-------o---o------oooooo--o----ooo--o--------o-------o----------------------------oo-oo-o--o--------
o--oo------o-----oo--o-oo------------oo--o------o--o-------------oo----o------------oooo-o------oo--
-----o----------ooooooooo--------------oo--------------oo-----o-----o-o--o------o----------o----o---
~~~~
