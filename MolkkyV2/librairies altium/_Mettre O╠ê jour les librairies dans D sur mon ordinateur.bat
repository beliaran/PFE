net use y: \\eseonas\depots\Altium
xcopy "y:\librairies altium" "D:\librairies altium" /f /i /e /s
net use y: /DELETE /YES
echo "la librairie altium a �t� mise � jour sur cet ordinateur ! Retrouvez l� dans D:\librairie Altium !" 
pause