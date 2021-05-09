import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogEditTargetaComponent } from './dialog-edit-targeta.component';

describe('DialogEditTargetaComponent', () => {
  let component: DialogEditTargetaComponent;
  let fixture: ComponentFixture<DialogEditTargetaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogEditTargetaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogEditTargetaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
