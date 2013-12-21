' Open notepad 
Set WshShell = WScript.CreateObject("WScript.Shell")
'WshShell.Run "notepad", 9

' Give Notepad time to load
WScript.Sleep 500 

'type in Hello World
WshShell.SendKeys WScript.Arguments(0)
