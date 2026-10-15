# SquirtleBot

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
2. Open the project into Intellij as follows:
   2Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/squirtlebot/SquirtleBot.java` file, right-click it, and choose `Run SquirtleBot.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
                                   16600000661                    
                            4009000000000000000000001             
                        7001   60000800000000000001               
                      90  0000000  0000000800000 1000000006       
                    60 700000000000 00800000006 0000000000000     
                   00 00  000000000 1080000000 000000000   000    
                  80 00   0000000004 080000880 0000000000  0000   
                 869 00000000000000 7080000000 0000000000000000   
                6660 00000000000000 00800000000 000000000000000   
                06661 00000000000  0000000000000 0000000000000    
               6666668   0000    00000000000000005 0000000000     
               666666666660800000000   780005    000         06   
               66666666656008880000               0800000000002   
               19666666628000000800               0808000000801   
                06466666560800000000 3333333337  0000000000000    
                 066666664000000000007  73337   00000000000806    
                  0666666560080000000000096600000000000000000     
                   69666664400000000008000000088000000000006      
                     0866664300000000000888880000000080000        
               16886    08966660000000000000000000000006          
            90966666606     769860000000000000000003              
           0666666666660  6006                      00            
          79666669666660 90006  900 888888883888888  001          
          16656  1  168  0000 1 00 88888888838888888 000          
           066666660 79 0008   80  08888888338888888 4000         
           186666660  0     73 00 333333333333333388  0           
             080800  00000  33 00 000000000858000000              
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.
