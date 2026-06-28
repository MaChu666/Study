import { describe, expect, it } from 'vitest'
import { normalizeVideoList } from '@/utils/videoList'

describe('normalizeVideoList', () => {
  it('normalizes backend list wrappers and bare arrays', () => {
    const wrapped = [{ videoId: 'BV1' }]
    const bare = [{ videoId: 'BV2' }]

    expect(normalizeVideoList({ list: wrapped })).toBe(wrapped)
    expect(normalizeVideoList(bare)).toBe(bare)
    expect(normalizeVideoList({})).toEqual([])
  })
})
