import { Component } from '@angular/core';
import { CameraPreview, CameraPreviewPictureOptions, CameraPreviewOptions, CameraPreviewDimensions } from '@ionic-native/camera-preview/ngx';

@Component({
  selector: 'app-tab1',
  templateUrl: 'tab1.page.html',
  styleUrls: ['tab1.page.scss']

})

export class Tab1Page {

  public picture;

  constructor(private cameraPreview: CameraPreview) { 
    this.pic();
  }

  pic() {
    const cameraPreviewOpts: CameraPreviewOptions = {
      x: 0,
      y: 0,
      width: window.screen.width,
      height: window.screen.height,
      camera: 'front',
      tapPhoto: true,
      previewDrag: true,
      toBack: true,
      alpha: 1
    }

    // start camera
    this.cameraPreview.startCamera(cameraPreviewOpts).then(
      (res) => {
        console.info(res)
      },
      (err) => {
        console.error(err)
      });


    // picture options
    const pictureOpts: CameraPreviewPictureOptions = {
      width: 1280,
      height: 1280,
      quality: 85
    }

    // take a picture
    this.cameraPreview.takePicture(pictureOpts).then((imageData) => {
      this.picture = 'data:image/jpeg;base64,' + imageData;
    }, (err) => {
      console.log(err);
      this.picture = 'assets/img/test.jpg';
    });


    // Switch camera
    this.cameraPreview.switchCamera();

    // set color effect to negative
    this.cameraPreview.setColorEffect('negative');

    // Stop the camera preview
    this.cameraPreview.stopCamera();
  }
}


