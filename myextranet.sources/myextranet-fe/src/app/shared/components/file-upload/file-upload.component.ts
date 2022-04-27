/**
 *     MyExtranet, il portale per collaborare con l’ente Regione Veneto.
 *     Copyright (C) 2022  Regione Veneto
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
import { Component, ElementRef, EventEmitter, HostListener, Input, Output, ViewEncapsulation } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { v4 as uuidv4 } from 'uuid';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss'],
  encapsulation: ViewEncapsulation.None,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: FileUploadComponent,
      multi: true
    }
  ]
})
export class FileUploadComponent implements ControlValueAccessor {

  @Input() hideLabel = false;
  @Input() downloadOn = false;
  @Input() showPreview = false;
  @Input() restrictedTypes: string[] = [];
  @Input() fileTypes: string[];
  @Input() downloadId: string = null;
  @Input() isReadOnly = false;
  private _isFo: boolean = true;
  @Input() set isFo(value) {
    this._isFo = value;
    this.color = value ? 'accent' : 'primary';
  }
  get isFo(): boolean {
    return this._isFo;
  }
  @Output() fileUpload: EventEmitter<string | ArrayBuffer> = new EventEmitter<string | ArrayBuffer>();

  public color: string;

  public fileInput: string = 'fileInput' + uuidv4();

  public file: File | null = null;
  fileName: string;
  public error: string = null;
  private onChange: (_) => void;

  public imageSrc: string | ArrayBuffer;

  @HostListener('change', ['$event.target.files']) emitFiles(event: FileList) {
    const file = event && event.item(0);
    if (this.restrictedTypes.length > 0) {      
      for (let i = 0; i < this.restrictedTypes.length; i++) {
        if (file.type === this.restrictedTypes[i]) {
          this.error = null;
          this.onChange(file);
          this.file = file;
          break;
        } else {
          this.error = 'Type mismatch';
        }
      }
      if (this.error) {
        this.writeValue(null);
        const uniqueTypes = [];
        const types = this.restrictedTypes.map(val => {
          return this.mimeMap(val)
        }).forEach(type => {
          if (uniqueTypes.indexOf(type) === -1) uniqueTypes.push(type)
        });
        this.error = `Il tipo di file deve essere uno tra i seguenti : ${uniqueTypes.join(', ')}`
      }
    } else {
      const reader = new FileReader();
      reader.onload = e => this.imageSrc = reader.result;

      reader.readAsDataURL(file);
      this.onChange(file);
      this.file = file;
      reader.addEventListener("load", () => {
        this.fileUpload.emit(this.imageSrc);
      }, false);

    }
  }

  clearFileInput() {
    const element: HTMLInputElement = document.getElementById(this.fileInput) as HTMLInputElement;
    element.value = null;
    this.file = null;
    this.fileName = null;
    this.imageSrc = null;
    this.fileUpload.emit(this.imageSrc);
    if (!!this.downloadId) this.downloadId = null;
    this.writeValue(null);
    this.onChange(null);
  }

  constructor(private host: ElementRef<HTMLInputElement>) { }

  writeValue(value: File | null) {
    // clear file input
    this.host.nativeElement.value = '';
    this.file = value;
    if (value) this.fileName = value.name;
    else this.fileName = null;
  }

  registerOnChange(fn: () => void) {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void) {
  }

  openFileInput(element_id: string) {
    this.clearFileInput();
    setTimeout(() => {

      const element = document.getElementById(this.fileInput);
      if (!!this.downloadId) this.downloadId = null;
      element.click();
    }, 120)
  }

  uploadFile() {
    this.writeValue(null);
  }

  mimeMap(type: string) {
    switch (type) {
      case 'application/pdf':
        {
          return 'pdf';
        }
      case 'image/gif':
        {
          return 'gif';
        }
      case 'image/jpeg':
        {
          return 'jpg';
        }
      case 'application/pkcs7-mime':
        {
          return 'p7m';
        }
      case 'application/x-pkcs7-mime':
        {
          return 'p7m';
        }
      default: {
        return type.split('/')[1];
      }
    }
  }

  downloadFileFromId() {
    if (!!this.downloadId) {
      window.open(this.downloadId);
    } else if (!!this.file) {
      //window.open(this.imageSrc);
      window.open(URL.createObjectURL(this.file));
    }
  }
}
