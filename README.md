SkinShuffle is great! But the textbox for importing a skin from a file is not very good. This fork addresses 2 issues:

### [#65 Ignore Quotation Marks](https://github.com/IMB11/SkinShuffle/issues/66)
When you right click on a file on windows, and click "Copy as path", it copies the file's path but with quotation marks around it. So when you're uploading a skin to use as a preset, you have to go to another textbox (because the SkinShuffle one doesn't allow for arrow keys to move the cursor), remove the quotations, then paste it into mc. I think it'd be great if SkinShuffle would ignore the quotation marks when they're pasted in with the file path.

### [#66 Typing Colon Crashes Game](https://github.com/IMB11/SkinShuffle/issues/65)
In the textbox used for typing in a file path, trying to type "C:" crashes the game, but if there's already a path in front of the colon it doesn't.
