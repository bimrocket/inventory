import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogDeleteTramComponent } from './dialog-delete-tram.component';

describe('DialogDeleteTramComponent', () => {
  let component: DialogDeleteTramComponent;
  let fixture: ComponentFixture<DialogDeleteTramComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogDeleteTramComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogDeleteTramComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
