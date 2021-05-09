import { FilterDeletedGuildPipe } from './filter-deleted-guild.pipe';

describe('FilterDeletedGuildPipe', () => {
  it('create an instance', () => {
    const pipe = new FilterDeletedGuildPipe();
    expect(pipe).toBeTruthy();
  });
});
