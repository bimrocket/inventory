import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogDetallsTramComponent } from './dialog-detalls-tram.component';

describe('DialogDetallsTramComponent', () => {
  let component: DialogDetallsTramComponent;
  let fixture: ComponentFixture<DialogDetallsTramComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogDetallsTramComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogDetallsTramComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
