-- 取消点赞原子操作：1.移除用户从点赞集合 2.递减点赞总数
-- 参数：1-用户点赞集合key 2-点赞总数key 3-用户名
local userSetKey = KEYS[1]
local countKey = KEYS[2]
local username = ARGV[1]

-- 判断用户是否点赞
local isLiked = redis.call('SISMEMBER', userSetKey, username)
if isLiked == 0 then
    return 0 -- 未点赞，返回0
end

-- 移除用户
redis.call('SREM', userSetKey, username)
-- 递减总数（保证总数不小于0）
local count = redis.call('DECR', countKey)
if count < 0 then
    redis.call('SET', countKey, 0)
end
return 1 -- 取消成功，返回1