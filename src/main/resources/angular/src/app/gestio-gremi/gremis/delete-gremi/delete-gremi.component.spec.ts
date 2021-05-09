import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteGremiComponent } from './delete-gremi.component';

describe('DeleteGremiComponent', () => {
  let component: DeleteGremiComponent;
  let fixture: ComponentFixture<DeleteGremiComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeleteGremiComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteGremiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
