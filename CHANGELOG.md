# CHANGELOG

1. First version: one phone, Arabic (text + voice) ⇄ Kirundi (text), Arabic read aloud.
2. Updates and stability (ideas from prayer-tv): fixed signing key so updates install over the old app,
   version = build number, release `vN` per build, update bar in the app, mark / restore stable version.
3. Update downloads into the app's cache and is deleted after the update; GitHub keeps only the newest and the stable version.
4. Ready phrases (20 + add / delete), history kept forever, back-translation under each translation, daily tasks screen (Kirundi + Arabic, tick done), Swahili mic for the maid, ⇅ flip / flat screen; translations cached for offline reuse.
5. Tasks screen: share as text (WhatsApp…) or as a PDF to print (Kirundi big, Arabic under it, tick boxes).
6. Picture tiles (🧹 أعمال): big emoji buttons for the maid, tap = Kirundi shown on top. Daily tasks (🔁 كل يوم) untick themselves each new day.
7. Quick replies: maid chips on her half (Yego, Oya, Murakoze, Ntabwo numvise, sick, hungry, need help) and 💬 ردود panel for her (sick?, hungry?, where?, slow down, danger).
8. ⚙️ Settings: night mode and text size (small / medium / large), kept on the phone.
9. Picture tiles now use simple SVG drawings (drawn in the app, no internet needed) instead of emoji.
10. Layout clears the phone's status bar (top) and navigation bar (bottom), on the main screen and in every panel.
11. Review fixes: night-mode text box readable; "clear done" keeps daily tasks; Back button closes the open panel first; bottom bar is two rows of four big buttons; typed text survives list refresh; failed translations are retried when a list opens; clear messages for "no internet" vs "service busy"; download and install errors reported separately; picture/quick taps no longer clear the box you were typing in.
12. Auto voice: every Kirundi translation is read aloud for the maid (Kirundi voice if the phone has it, else Swahili); Arabic read with a female voice when the phone has one. Ready phrases use feminine address (كيف حالِك، إجازتِك).
13. New soft colours (sage green and warm clay on cream; the same idea in night mode), soft buttons, matching printed PDF.
14. ⚙️ Settings in tabs: 🎨 colours (soft, blue, pink), 🔤 text size (small, medium, large, extra large), 🌙 night mode, 🔄 update (checks the latest version and shows the result, with a download button when there is a new one).
15. Picture tiles show the Kirundi name under the Arabic one, so the maid can read what is pointed at.
16. The 🔊 button now reads the Kirundi translation aloud (it used to read the Arabic one).
17. Kirundi voice built in: on first use the app downloads a Kirundi voice model (Meta MMS, about 60 MB) once, then reads Kirundi offline. Order: phone Kirundi voice, built-in voice, Swahili voice. NOT yet tested on a phone (the model host was unreachable from the build environment).
18. The Kirundi voice model is added to the APK at build time (not downloaded by the phone), so updates keep it. Not yet tested on a phone.
19. Family (👨‍👩‍👧 العائلة): each person picks a name once (mom, dad, child, maid); chats list and bubbles; quick replies; 🔔 ring that opens a full-screen alert with a loud tone and vibration; messages and rings sent through ntfy.sh with a shared family code; languages Arabic / Kirundi / English, maid starts in Kirundi. Ring and messages reach the phone only while Mani is open (background full-screen ring needs a native Android step, next).
20. Family members use their names: رند (mom), أميه (dad), حسان, سلمى, عبدالملك (children), and the maid.
21. Paste from WhatsApp: 📋 on each side pastes the clipboard and translates at once (long-press paste also translates). 📤 واتساب under each translation copies it and opens the share sheet (WhatsApp).
22. Review fixes: the ring sounds after any tap (phone sound rules), a loud repeating vibration, family code at least 12 characters, language buttons on the first family screen (maid starts in Kirundi), the emergency tile also rings the family, updates offer the stable version (file STABLE).
