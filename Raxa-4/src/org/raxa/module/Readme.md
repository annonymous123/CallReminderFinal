The code is independednt of any TTS or audio voice.

Morever its flexible and provides option how each word is going to be converted to 
voice(either using TTS or pre recorded voice)

If the mode for the word is 1 it will use TTS and if mode is 2 it will look for 
audio file for that part.
eg. a typical entry in database is: 
{"field":"header1","content":"This is your Medicine Reminder.Now you have to take","mode":2}

Since the mode is 2 it will look at the language(preferLanguage of patient) properties file and search for audio Folder 
under name "header1" and play that file

had the "mode' been 1 it would have played it using TTS.


Note:Folder SoundConverter is of no use.




