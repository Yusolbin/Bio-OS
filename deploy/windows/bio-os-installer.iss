#define MyAppName "BIO-OS"
#define MyAppVersion "1.0"
#define MyAppPublisher "Yusolbin"
#define MyAppURL "https://github.com/Yusolbin/Bio-OS"
#define SourceDir "D:\CoreSync\Bio_OS\deploy\windows\BIO-OS"

[Setup]
AppId={{B7F7D71F-8C6F-4E2B-9B54-5E87E22A0625}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}

DefaultDirName={localappdata}\BIO-OS
DefaultGroupName=BIO-OS
DisableProgramGroupPage=yes

OutputDir=D:\CoreSync\Bio_OS\deploy\windows\installer
OutputBaseFilename=BIO-OS-Setup

Compression=lzma
SolidCompression=yes
WizardStyle=modern

PrivilegesRequired=lowest
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible

UninstallDisplayName=BIO-OS
SetupLogging=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "Create a desktop shortcut"; GroupDescription: "Additional icons:"; Flags: unchecked

[Files]
Source: "{#SourceDir}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\BIO-OS"; Filename: "{app}\start-bio-os.bat"; WorkingDir: "{app}"
Name: "{userdesktop}\BIO-OS"; Filename: "{app}\start-bio-os.bat"; WorkingDir: "{app}"; Tasks: desktopicon

[Run]
Filename: "{app}\start-bio-os.bat"; Description: "Run BIO-OS"; Flags: nowait postinstall skipifsilent