(and (full player_movingaverageaccum1) 
   (greaterthan (subtract (aggregate player_movingaverageaccum3 "add") 1) (divide system_argumentIndex 2))
   (wincheck player_movingaverageaccum1 player_movingaverageaccum2 system_rank (divide system_argumentIndex 3))
   (greaterthan (probabilityas player_movingaverageaccum1 player_movingaverageaccum2) 
	  (add 50 (multiply 100 system_rank))))
      	   				