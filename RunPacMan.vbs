
Set WshShell = CreateObject("WScript.Shell")

WshShell.CurrentDirectory = "C:\Users\JANJAN\OneDrive\Documents\pacman-java-master"

WshShell.Run """C:\Users\JANJAN\OneDrive\Documents\pacman-java-master\RunPacMan.bat""", 0, False
