# MenuShops
## The only Minecraft GUI shop plugin you'll ever need, featuring fully customizable messages and GUI

<img src="https://github.com/user-attachments/assets/73a4da74-d03d-4ca7-ad7e-0474459b787f" height="300">
<img src="https://github.com/user-attachments/assets/ca29f6ad-85e5-4d37-a6a6-23bea0324afd" height="300">

## Commands:
`/menushops reload` Reloads the configuration, the shops and the external Hooks. (See Hooks)<br>
`/menushops create <shop-name> "<shop-title>"` Creates a new MenuShop with a custom title (supports [MiniMessage](https://docs.advntr.dev/minimessage/index.html))<br>
`/menushops delete <shop-name>` Deletes a MenuShop<br>
`/menushops edit <shop-name> additem <shop_buys|shop_sells> <lower-bound> <upper-bound> <@hand | <itemstack> <id>>` Adds the item that the player is currently holding in their hand to a MenuShop. For <lower-bound> and <upper-bound> see Values<br>
`/menushops edit <shop-name> removeitem <buy|sell> <id>` Removes a item from a MenuShop<br>

## Hooks
MenuShops can hook into multiple additional plugins to enhance the experience.
Currently supported:
- [PlaceholderAPI (Messages)](https://www.spigotmc.org/resources/placeholderapi.6245/)
- [Vault (Payment method)](https://www.spigotmc.org/resources/vault.34315/)
- [CoinsEngine (Payment method)](https://www.spigotmc.org/resources/coinsengine-%E2%AD%90-economy-and-virtual-currencies.84121/)

## Values
MenuShops uses a System called "Values" to determine the worth of a item.
Currently three types of values are supported:
- Materials
- Vault Economy
- CoinsEngine Currencies

Note: At the moment only the lower-bound value is used, but a shop requires both values to be compatible with an upcomming random value system. 

Each value type has its own parameters, however, the syntax is the same for all of them.
In general the value syntax goes as follows:
`<valuetype>#<parameter>:<amoun>t`<br>

### Material Value:
General: `material#<material-type>:<amount>`<br>
with `<material-type>` being any minecraft item material and `<amount>` being an integer between 0 and 64

e.g: `material#stone:10` or `material#oak_planks:64`<br>

### Vault Value
General: `vault#money:<amount>`<br>
with `<amount>` being an integer above 0

e.g: `vault#money:0` (free item) or `vault#money:100`<br>

### CoinsEngine Value
General: `cloinsengine#<currency>:<amount>`<br>
with `<currency>` being a valid and setup CoinsEngine currency and `<amount>` being an integer above 0

e.g: `coinsengine#money:420` or `coinsengine#votecoins:69`<br>
