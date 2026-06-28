export function normalizeVideoList(data) {
  if (Array.isArray(data?.list)) {
    return data.list
  }
  if (Array.isArray(data)) {
    return data
  }
  return []
}
