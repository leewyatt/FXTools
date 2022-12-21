## For: English | [中文](README_zh.md) User.

## FXTools

A practical tool developed using JavaFX; Software development for PC or mobile; Image tools,Color tools,Svg Tools, Font tools, Library and so on....<br />

**Download**
- [Download From **Github**](https://github.com/leewyatt/FXTools/releases)
- [Download From **Gitee**](https://gitee.com/leewyatt/FXTools/releases)

Youtube Video Introduction: https://youtu.be/lDj1Wa_2IfM

- [FXTools](#fxtools)
    * [FXTools_Doc](#tools-doc)
        + [Screenshots](#screenshots)
        + [Image Tools](#image-tools)
        + [Color Tools](#color-tools)
        + [SVG Tools](#svg-tools)
        + [Font Tools](#font-tools)
        + [Library](#library)
        + [Tips](#tips)
        + [Thanks](#thanks)
        + [Donate](#donate)

I wrote an IDEA plug-in named Java FXTools before, but the API of IDEA will change, and I don’t have the time and energy to maintain it all the time, so I made this stand-alone version;Rewrite the code, re-layout, and add new functions;<br />
The project uses java17 for development, but is trying to be compatible with java8 (except for screenshot-related APIs).
So it doesn't use too many advanced syntax features.  <br />
The JDK used is **Liberica 17** now. <br />
### FXTools_Doc

<span id="tools-doc" ></span>
A practical tool developed using Java FX, software development for PC or mobile, the main functions are as follows:

<span id="screenshots" ></span>
![](readme_imgs/yl_1.png)
![](readme_imgs/yl_2.png)
![](readme_imgs/yl_3.png)
![](readme_imgs/yl_4.png)
![](readme_imgs/yl_cn_4.png)
![](readme_imgs/yl_cn_1.png)
![](readme_imgs/yl_cn_2.png)
![](readme_imgs/yl_cn_3.png)

#### Image Tools

<span id="image-tools" ></span>

- [1] App Icon Generator: Supports icon generation for Windows, MacOS, Linux, iPhone, iPad, watchOS, Android and
  other systems;( icon, icns, png...)
- [2] Image Sets Generator: Support to generate multiple images (eg. 1x,2x,3x...) of javafx, ios, android;
- [3] Format Converter: Support common image format conversion;eg. svg, webp, png, bmp, jpg, gif.
- [4] Gif Decoder: Decompose the Gif animation into frame-by-frame pictures; (mainly convenient for game engines
  such as FXGL).
- [5] Image stitching: Splicing multiple pictures into one picture, improving efficiency and reducing the number of
  io; (mainly convenient for game engines such as FXGL).
- [6] Screenshot; take a picture of a specified location on the screen;
  (*The screenshots of the java 9+ version are clear; java 8 cannot capture high-definition resolution screens, so the
  screenshots are too small under high-resolution screens; If modify the source code to support java 8, only need to
  modify a few lines of code related to the screenshot.)

#### Color Tools

<span id="color-tools" ></span>

- [1] Absorb the color of the specified position on the screen;
- [2] The selected color can generate fx CSS code or java code;
- [3] 20+ pages of color matching reference;
- [4] Convert between multiple color formats: HSB,RGB,HSL,Hex

#### SVG Tools

<span id="svg-tools" ></span>

- [1] Support preview of SVG Path; easy to view the display effect of svg under fx;
- [2] It is convenient to extract the Path attribute in the SVG file, which is convenient for use in FX;
- [3] Generate fx css code or java code;

#### Font Tools

<span id="font-tools" ></span>

- [1] Preview the font effect that comes with the system;
- [2] Support adding external fonts. Preview;
- [3] Generate fx css code or java code;

#### Library

<span id="library" ></span>

- [1] Reference[AwesomeJavaFX](https://github.com/mhrimaz/AwesomeJavaFX)Lists many great open source libraries, books,
  etc.;

#### Tips

<span id="tips" ></span>

- [1] When processing images, multi-threading is supported; the number of threads can be set on the settings page; the
  default is 2 threads;
- [2] Turn off the image preview option and turn off the parse image size option in the settings page, Will get a faster loading speed of the image;
- [3] Turn off parsing image size and generating thumbnails when loading images, which can speed up image processing;
- [4] After the image processing is completed, the output directory of the image will be opened by default;
- [5] This tool support DarkMode and LightMode;

#### Thanks
<span id="thanks" ></span>

|                                                                                                                                                                                                                                                                                                  |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| <img src="https://www.ej-technologies.com/images/product_banners/install4j_large.png" width="128"> Thanks to [ej-technologies](https://www.ej-technologies.com/) for their [open source license](https://www.ej-technologies.com/buy/install4j/openSource). We use Install4j to build installers.|
| <img src="https://gluonhq.com/wp-content/uploads/2015/01/gluon_logo@2x.png" width="5%">Thanks to [Gluon](https://gluonhq.com/) for documents.                                                                                                                                                    |
| <img src="https://gluonhq.com/wp-content/uploads/2015/02/SceneBuilderLogo@2x.png" width="5%">Thanks to [SceneBuilder](https://github.com/gluonhq/scenebuilder) Used ColorPicker, DoubleTextField etc.                                                                                            |
| <img src="https://controlsfx.github.io/images/ControlsFX.png" width="15%">Thanks to [controlsfx](https://github.com/controlsfx/controlsfx)                                                                                                                                                       |
| Thanks to [@Abhinay Agarwal](https://github.com/abhinayagarwal)for help.                                                                                                                                                                                                                         |
| Thanks to [@黑羽](https://blog.thetbw.xyz/) for providing the storage.                                                                                                                                                                                                                            |
| Thanks to [@Anivie](https://github.com/Anivie) for testing, documentation, etc.;                                                                                                                                                                                                                 |
| Thanks to [openjfx](https://openjfx.io/) for documents, sample project, maven plugin, etc .                                                                                                                                                                                                      |
| Thanks to [AwesomeJavaFX](https://github.com/mhrimaz/AwesomeJavaFX)                                                                                                                                                                                                                              |                                                                                                                                                                                                     |
| Thanks to [guava](https://github.com/google/guava)                                                                                                                                                                                                                                               |
| Thanks to [gson](https://github.com/google/gson)                                                                                                                                                                                                                                                 |
| Thanks to [webp-imageio](https://github.com/sejda-pdf/webp-imageio) for image processing.                                                                                                                                                                                                        |
| Thanks to [thumbnailator](https://github.com/coobird/thumbnailator) for image processing.                                                                                                                                                                                                        |
| Thanks to [image4j](https://github.com/imcdonagh/image4j) for image processing.                                                                                                                                                                                                                  |
| Thanks to [Apache Commons Imaging](https://github.com/apache/commons-imaging) for image processing.                                                                                                                                                                                              |
| Thanks to [batik](https://github.com/apache/xmlgraphics-batik) for svg processing.                                                                                                                                                                                                               |
| Thanks to [animated-gif-lib](https://github.com/rtyley/animated-gif-lib-for-java) for image processing.                                                                                                                                                                                          |
| Thanks to [TwelveMonkeys](https://github.com/haraldk/TwelveMonkeys) for image processing.                                                                                                                                                                                                        |
| Thanks to [ICNS](https://github.com/gino0631/icns) for image processing.                                                                                                                                                                                                                         |
| Thanks to [web_color](https://gitee.com/song-xiansen/web_color)for color matching.                                                                                                                                                                                                               |
| Thanks to various references on the internet.                                                                                                                                                                                                                                                    |
<br />

#### Support and Donations

<span id="donate" ></span>

You can contribute and support this project by doing any of the following:
* Star the project on GitHub
* Give feedback
* Commit PR
* Contribute your ideas/suggestions
* Share FXTools with your friends/colleagues
* If you like FXTools, please consider donating: <br />
  <a href="https://www.buymeacoffee.com/fxtools" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" style="height: 60px !important;width: 217px !important;" ></a> <br />
  ![](src/main/resources/images/donate/wx.png) <br />
  ![](src/main/resources/images/donate/zfb.png) <br />

  **Note:** After using Alipay/WeChat to pay for your donation, please provide your name/nickname and website by leaving
  a message or via email in the following format:

  `Name/Nickname [<website>][: message]` (website and message are optional.)

  Example: `LeeWyatt <github.com/leewyatt>: I like fxtools!`

  If you choose to send an email, please also provide the following information:
  ```text
  Donation Amount: <amount>
  Payment Platform: Alipay/WeChat Pay
  Payment Number (last 5 digits): <number>
  ```
  Email address: [leewyatt7788@gmail.com][mailto] (click to send email)

  The name, website and total donation amount you provide will be added to the [donor list] <br />
**Thank you for your support!** 

| **Name**   | **Website** | **Message** | **Amount** |
|------------|-------------|-------------|------------|
| CierConnor |             | 真不错       | 200 CNY    |
| 守望者      |           |             | 6.66 CNY   |
| 随风Kiki   |             | 喝杯可乐        | 3 CNY      |