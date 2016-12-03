# Jackson Json
-keepattributes *Annotation*,EnclosingMethod

-keepnames class com.fasterxml.jackson.** { *; }

-dontwarn javax.xml.**
-dontwarn javax.xml.stream.events.**
-dontwarn com.fasterxml.jackson.databind.**
