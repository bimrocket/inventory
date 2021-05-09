import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogPosicioTiquetComponent } from './dialog-posicio-tiquet.component';

describe('DialogPosicioTiquetComponent', () => {
  let component: DialogPosicioTiquetComponent;
  let fixture: ComponentFixture<DialogPosicioTiquetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogPosicioTiquetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogPosicioTiquetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
