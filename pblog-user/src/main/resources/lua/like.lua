-- 点赞原子操作：1.添加用户到点赞集合 2.递增点赞总数
-- 参数：1-用户点赞集合key 2-点赞总数key 3-用户名
local userSetKey = KEYS[1]
local countKey = KEYS[2]
local username = ARGV[1]

-- 判断用户是否已点赞
local isLiked = redis.call('SISMEMBER', userSetKey, username)
if isLiked == 1 then
    return 0 -- 已点赞，返回0
end

-- 添加用户到集合
redis.call('SADD', userSetKey, username)
-- 递增总数
redis.call('INCR', countKey)
return 1 -- 点赞成功，返回1