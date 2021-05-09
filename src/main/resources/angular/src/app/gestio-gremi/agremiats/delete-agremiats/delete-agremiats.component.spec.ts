import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteAgremiatsComponent } from './delete-agremiats.component';

describe('DeleteAgremiatsComponent', () => {
  let component: DeleteAgremiatsComponent;
  let fixture: ComponentFixture<DeleteAgremiatsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeleteAgremiatsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteAgremiatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
