# JavaFX Image Cropper

![Version](https://img.shields.io/badge/version-26.1.0-blue)

A lightweight, reusable **JavaFX image cropping component** with fixed aspect ratio support, smooth zooming, and drag-to-position interaction.

Designed to be embedded directly into JavaFX layouts and return a ready-to-use cropped image.

![Preview](docs/preview.gif)

---

## ✨ Features

* 🖱 Drag the image to position it within the crop area
* 🔍 Smooth zoom control via slider
* 📐 Fixed aspect-ratio crop window
* 🖼 Transparent background output

---

## 📦 Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.rossrao.libs.imagecropper:javafx-image-cropper:26.1.0")
}
```

> **Note:** JavaFX is intentionally **not bundled** with this library.
> Consumers are expected to manage JavaFX themselves.

---

## 🔧 Required JavaFX Modules

Your application must include the following JavaFX modules at runtime:

* `javafx.controls`
* `javafx.graphics`

If you’re using the JavaFX Gradle plugin:

```kotlin
javafx {
    version = "17"
    modules = listOf("javafx.controls", "javafx.graphics")
}
```

---

## 🚀 Usage

### Basic Example

```java
Image image = new Image("file:photo.jpg");

// Create a square (1:1) cropper
ImageCropper cropper = new ImageCropper(image, 1, 1);

// Add to your layout
root.getChildren().add(cropper);

// When ready to export
WritableImage croppedImage = cropper.snap();
```

The returned image:

* is tightly cropped to the crop window
* is safe to save, upload, or convert

---

## 📐 Aspect Ratio

The crop area uses a **fixed aspect ratio** defined at construction time:

```java
// 16:9 crop
new ImageCropper(image, 16, 9);

// 4:3 crop
new ImageCropper(image, 4, 3);
```

At this time, the crop window is:

* fixed in size
* centered
* not resizable

(This is by design to keep the component predictable and simple.)

---

## 🧠 Design Notes

* The image is clamped so the crop area is **never empty**
* Zooming preserves the image center
* The crop overlay is purely visual and excluded from snapshots
* The API is intentionally small to avoid breaking changes
* `snap()` is the primary method to retrieve the cropped image

---

## 🧪 Java Version

* **Java:** 17+
* **JavaFX:** 17+

---

## 📄 License

MIT License
