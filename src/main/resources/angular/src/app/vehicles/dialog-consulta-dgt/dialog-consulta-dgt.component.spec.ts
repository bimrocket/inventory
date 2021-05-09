import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogConsultaDGTComponent } from './dialog-consulta-dgt.component';

describe('DialogConsultaDGTComponent', () => {
  let component: DialogConsultaDGTComponent;
  let fixture: ComponentFixture<DialogConsultaDGTComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogConsultaDGTComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogConsultaDGTComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
