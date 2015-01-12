(1) To run the program:
java -jar hpwd.jar 
or
java -jar hpwd_debug.jar 

*hpwd_debug.jar has DEBUG boolean set to true to provide more inforamtion. The two program’s functions are all the same. 

========================================================

(2) Try to create accout:
Selete 2, to create account
Input user name: ‘rick’.*
Then input any password you want. 

*Because first, you cannot overwrite existing account, (you actually can, but need to change code)
and second, in order to make it work, there should be a corresponding username in feature file “./ress/input”, and there is already a username called ‘rick’ with feature values. 

========================================================

(3) To login
Selete 1, to login
Input user name ‘rick’, and corresponding password you created. 
After successfully login 3 times, harden password process will start to work.
Then changing ./ress/input file’s corresponding user feature values (largely change, 1-100 scale), will reault in false authentication. 

