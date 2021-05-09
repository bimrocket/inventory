import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogAssignTargetaComponent } from './dialog-assign-targeta.component';

describe('DialogAssignTargetaComponent', () => {
  let component: DialogAssignTargetaComponent;
  let fixture: ComponentFixture<DialogAssignTargetaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogAssignTargetaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogAssignTargetaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
