import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeletePermisosComponent } from './delete-permisos.component';

describe('DeletePermisosComponent', () => {
  let component: DeletePermisosComponent;
  let fixture: ComponentFixture<DeletePermisosComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeletePermisosComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeletePermisosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
