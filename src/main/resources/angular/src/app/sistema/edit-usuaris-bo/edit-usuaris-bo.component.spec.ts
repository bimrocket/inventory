import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditUsuarisBoComponent } from './edit-usuaris-bo.component';

describe('EditUsuarisBoComponent', () => {
  let component: EditUsuarisBoComponent;
  let fixture: ComponentFixture<EditUsuarisBoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditUsuarisBoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditUsuarisBoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
