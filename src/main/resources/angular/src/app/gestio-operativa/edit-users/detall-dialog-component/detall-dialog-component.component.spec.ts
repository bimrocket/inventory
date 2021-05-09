import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DetallDialogComponentComponent } from './detall-dialog-component.component';

describe('DetallDialogComponentComponent', () => {
  let component: DetallDialogComponentComponent;
  let fixture: ComponentFixture<DetallDialogComponentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DetallDialogComponentComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DetallDialogComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
