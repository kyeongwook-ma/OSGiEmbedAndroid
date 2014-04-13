SET DX="C:\Users\se\android-sdks\build-tools\17.0.0\dx.bat"
SET AAPT="C:\Users\se\android-sdks\build-tools\17.0.0\aapt.exe"

for %%j in (*.jar) do %DX% --dex --output=%CD%\classes.dex "%CD%\%%j" && %AAPT% add "%CD%\%%j" classes.dex && del classes.dex
