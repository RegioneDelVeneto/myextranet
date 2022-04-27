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
import { ComponentFactory, ComponentFactoryResolver, ComponentRef, Directive, Input, TemplateRef, ViewContainerRef } from '@angular/core';
import { LoaderComponent } from './loader/loader.component';

@Directive({
  selector: '[appLoading]'
})
export class LoadingDirective {
  loadingFactory: ComponentFactory<LoaderComponent>;
  loadingComponent: ComponentRef<LoaderComponent>;

  @Input()
  set appLoading(loading: boolean) {
    this.vcRef.clear();

    if (loading) {
      // create and embed an instance of the loading component
      this.loadingComponent = this.vcRef.createComponent(this.loadingFactory);
    } else {
       // embed the contents of the host template
      this.vcRef.createEmbeddedView(this.templateRef);
    }
  }

  constructor(
    private templateRef: TemplateRef<any>,
    private vcRef: ViewContainerRef, private componentFactoryResolver: ComponentFactoryResolver) {
    // Create resolver for loading component
    this.loadingFactory = this.componentFactoryResolver.resolveComponentFactory(LoaderComponent);
  }

}
