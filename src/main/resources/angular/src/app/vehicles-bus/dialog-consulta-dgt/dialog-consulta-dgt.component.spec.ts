import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogConsultaDGTBusComponent } from './dialog-consulta-dgt.component';

describe('DialogConsultaDgtComponent', () => {
  let component: DialogConsultaDGTBusComponent;
  let fixture: ComponentFixture<DialogConsultaDGTBusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogConsultaDGTBusComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogConsultaDGTBusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
