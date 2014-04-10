#d:
#cd D:\workspace\music-android\travelassistant.android\res\raw
SET DX="C:\Program Files (x86)\Android\android-sdk\build-tools\18.0.1\dx.bat"
SET AAPT="C:\Program Files (x86)\Android\android-sdk\build-tools\18.0.1\aapt.exe"

for %%j in (*.jar) do %DX% --dex --output=%CD%\classes.dex "%CD%\%%j" && %AAPT% add "%CD%\%%j" classes.dex && del classes.dex
